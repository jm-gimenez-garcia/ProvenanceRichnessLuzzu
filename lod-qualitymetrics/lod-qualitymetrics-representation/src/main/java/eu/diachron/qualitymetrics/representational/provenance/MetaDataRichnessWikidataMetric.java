package eu.diachron.qualitymetrics.representational.provenance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import de.unibonn.iai.eis.diachron.semantics.DQM;
import de.unibonn.iai.eis.luzzu.assessment.QualityMetric;
import de.unibonn.iai.eis.luzzu.datatypes.ProblemList;
import de.unibonn.iai.eis.luzzu.exceptions.ProblemListInitialisationException;

public class MetaDataRichnessWikidataMetric implements QualityMetric {
	
	static final String METRIC_URI = "http://www.diachron-fp7.eu/dqm#MetaDataRichnessWikidataMetric";
	static Logger logger = LoggerFactory.getLogger(MetaDataRichnessWikidataMetric.class);
	List<Model> problemList = new ArrayList<Model>();
	
	// private static final String PROPERTY_STATEMENT = "^(http://www.wikidata.org/entity/P\\d+s)$";
	static final String PROPERTY_VALUE = "^(http://www.wikidata.org/entity/P\\d+v)$";
	static final String PROPERTY_QUALIFIER = "^(http://www.wikidata.org/entity/P\\d+q)$";
	static final String PROPERTY_REFERENCE = "^(http://www.wikidata.org/entity/P\\d+r)$";
	
	HashSet<String> values;
	HashSet<String> qualifiers;
	HashSet<String> references;

	@Override
	public void compute(Quad quad) {
		//logger.debug("Assessing quad: " + quad.asTriple().toString());
		try {
				if (quad.getPredicate().getURI().matches(PROPERTY_VALUE)) {
					values.add(quad.getSubject().getURI());
				} else if (quad.getPredicate().getURI().matches(PROPERTY_QUALIFIER)) {
					qualifiers.add(quad.getSubject().getURI());
				} else if (quad.getPredicate().getURI().matches(PROPERTY_REFERENCE)) {
					references.add(quad.getSubject().getURI());
				}
		} catch (Exception exception) {
			logger.error(exception.getMessage());
		}
	}

	@Override
	public Resource getAgentURI() {
		return DQM.LuzzuProvenanceAgent;
	}

	@Override
	public Resource getMetricURI() {
		return ModelFactory.createDefaultModel().createProperty(METRIC_URI);
	}

	@Override
	public ProblemList<?> getQualityProblems() {
		ProblemList<Model> tmpProblemList = null;
		try {
			if(this.problemList != null && this.problemList.size() > 0) {
				tmpProblemList = new ProblemList<Model>(this.problemList);
			} else {
				tmpProblemList = new ProblemList<Model>();
			}		} catch (ProblemListInitialisationException problemListInitialisationException) {
			logger.error(problemListInitialisationException.getMessage());
		}
		return tmpProblemList;
	}

	@Override
	public boolean isEstimate() {
		return false;
	}

	@Override
	public double metricValue() {
		double value = (double) (qualifiers.size()+references.size())/(values.size()+qualifiers.size()+references.size());
		logger.debug("Metric value: " + value);
		return value;
	}

}
