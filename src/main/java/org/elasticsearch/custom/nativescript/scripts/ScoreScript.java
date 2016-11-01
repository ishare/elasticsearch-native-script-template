package org.elasticsearch.custom.nativescript.scripts;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Scorer;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.script.AbstractSearchScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;
import org.elasticsearch.search.lookup.IndexField;
import org.elasticsearch.search.lookup.IndexFieldTerm;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhu on 11/1/16.
 *
 * You can extends AbstractExecutableScript or AbstractSearchScript,
 * also AbstractLongSearchScript and AbstractDoubleSearchScript
 */
public class ScoreScript extends AbstractSearchScript {
    public final static String SCRIPT_NAME = "score_script";
    private static Logger logger = Loggers.getLogger(SCRIPT_NAME);

    // the field containing the terms that should be scored, must be initialized
    // in constructor from parameters.
    private String field = null;
    // terms that are used for scoring
    private List<String> terms = null;
    // weights, in case we want to put emphasis on a specific term.
    private List<Double> weights = null;

    /**
     * @param params terms that a scored are placed in this parameter. Initialize them here.
     */
    @SuppressWarnings("unchecked")
    private ScoreScript(Map<String, Object> params) {
        params.entrySet();
        // get the terms
        terms = (List<String>) params.get("terms");
        weights = (List<Double>) params.get("weights");
        // get the field
        field = (String) params.get("field");

        if (terms == null || weights == null || field == null || terms.size() != weights.size()) {
            logger.error("init ScoreScript error");
        }
    }

    @Override
    public void setScorer(Scorer scorer) {
        // ignore
    }

    @Override
    public Object run() {
        // TODO: customize your own needs, below is just an example

        float score = 0;
        try {
            // first, get the IndexField object for the field.
            IndexField indexField = indexLookup().get(field);
            for (int i = 0; i < terms.size(); i++) {
                // Now, get the IndexFieldTerm object that can be used to access all
                // the term statistics
                IndexFieldTerm indexFieldTerm = indexField.get(terms.get(i));
                // compute the most naive tfidf and add to current score
                int df = (int) indexFieldTerm.df();
                int tf = indexFieldTerm.tf();
                if (df != 0 && tf != 0) {
                    score += weights.get(i) * (float) indexFieldTerm.tf() *
                            Math.log(((float) indexField.docCount() + 2.0) / ((float) df + 1.0));
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return score;
    }

    /**
     * Factory that is registered in
     * {@link org.elasticsearch.custom.nativescript.plugin.NativeScriptPlugin#getNativeScripts()}
     * method when the plugin is loaded.
     */
    public static class Factory implements NativeScriptFactory {

        /**
         * This method is called for every search on every shard.
         *
         * @param params list of script parameters passed with the query
         * @return new native script
         */
        @Override
        public ExecutableScript newScript(@Nullable Map<String, Object> params) {
            return new ScoreScript(params);
        }

        @Override
        public boolean needsScores() {
            return false;
        }

        @Override
        public String getName() {
            // this name will be used in the query `script_score` as `inline`
            return SCRIPT_NAME;
        }
    }
}
