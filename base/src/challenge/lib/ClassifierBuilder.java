package challenge.lib;

public interface ClassifierBuilder {
	Classifier training(Iterable<TaggedReview> data) ;
}
