package challenge.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
	private static final String PROPERTY_FILE = "server.properties";

	private final File incomingDir;
	private final File finishedDir;
	private final File resultsFile;

	public static void main(String[] args) throws IOException, InterruptedException {
		Main main = new Main();
		main.run();
	}


	private Main() throws IOException {
		InputStream in = new FileInputStream(PROPERTY_FILE);
		Properties props = new Properties();
		props.load(in);
		in.close();

		String incomingStr    = readMandatory(props, "incoming.dir");
		String finishedStr    = readMandatory(props, "finished.dir");
		String resultsStr = readMandatory(props, "results.file");

		incomingDir = initDirectory(incomingStr);
		finishedDir = initDirectory(finishedStr);

		File file = new File(resultsStr);
		initDirectory(file.getParent());		
		resultsFile = file;
	}

	public  void run() throws IOException, InterruptedException {
		while (true) {
			File[] jars = incomingDir.listFiles(jarFilter());
			for (File jar : jars) {
				processJar(jar);
			}
			
			if (jars.length == 0)
				Thread.sleep(5000);
		}
	}

	private static void runJar(File jar, File resultsFile) {
		File javaHome = new File(System.getProperty("java.home"));
		File javaBin = new File(javaHome, "bin/java");

		String cp = System.getProperty("java.class.path");

		String[] cmd = new String[] {javaBin.getPath(), "-Djava.security.manager", "-Djava.security.policy=server.policy", "-Xmx200m", "-cp", cp, Runner.class.getName() , jar.getPath(), resultsFile.getPath()};

		try {
			new ProcessBuilder(cmd).inheritIO().start().waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace(System.err);
		}
	}

	private static File initDirectory(String dirName) throws IOException {
		File dir = new File(dirName);
		dir.mkdirs();
		if (dir.isDirectory()) return dir;
		throw new IOException("Invalid directory: " + dir);
	}

	private void processJar(final File jar) {
		System.out.println("Executing jar: " + jar);

		runJar(jar, resultsFile);

		jar.renameTo(new File(finishedDir, jar.getName()));
	}


	private static FilenameFilter jarFilter() {
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.regionMatches(true, name.length()-4, ".jar", 0, 4);
			}
		};
	}

	private static String readMandatory(Properties props, String key) throws IOException {
		String result = props.getProperty(key);
		if (result != null) return result;
		throw new IOException("Property not found: " + key);
	}
}
