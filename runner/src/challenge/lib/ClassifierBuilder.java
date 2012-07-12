package challenge.lib;

public abstract class ClassifierBuilder {

	public abstract Classifier training(Iterable<TaggedReview> data) ;
}
