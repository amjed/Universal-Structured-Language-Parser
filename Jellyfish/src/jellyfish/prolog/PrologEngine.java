/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.prolog;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Theory;
import alice.tuprolog.Var;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jellyfish.triplestore.model.Relationship;
import jellyfish.triplestore.model.Triple;

/**
 *
 * @author Xevia
 */
public class PrologEngine {

    private StringBuilder builder;
    private List<Theory> theories;
    private Prolog prolog;

    public PrologEngine() {
        this.builder = new StringBuilder(6000);
        this.theories = new ArrayList<Theory>();
        clear();
    }

    public void define(Relationship rel, List<Triple> triples)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(
                PrologTemplates.getRelationshipHeader(
                    rel.getName(),
                    rel.isTransitive(),
                    rel.isSymmetric()
                ));

        for (Triple t:triples) {
            sb.append(PrologTemplates.getRelationshipAssertedName(t.getPredicate().getName())).
                    append("(").
                    append(t.getSubject().getName()).
                    append(",").
                    append(t.getObject().getName()).
                    append(").\n");
        }

        sb.append(
                PrologTemplates.getRelationshipDeclaration(
                    rel.getName(),
                    rel.isTransitive(),
                    rel.isSymmetric()
                ));

        String theory = sb.toString();

        this.builder.append(theory);
        try {
            this.theories.add(new Theory(theory));
        } catch (InvalidTheoryException ex) {
            Logger.getLogger(PrologEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    final public void clear()
    {
        this.builder.setLength(0);
        this.theories.clear();
        
        String setsCode = PrologTemplates.getSetsCode();
        this.builder.append(setsCode);
        try {
            this.theories.add(new Theory(setsCode));
        } catch (InvalidTheoryException ex) {
            Logger.getLogger(PrologEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPrologCode() {
        return this.builder.toString();
    }

    public PrologEnglet compile() throws Exception {
        Prolog prolog = new Prolog();
        for (Theory t:theories) {
            prolog.addTheory(t);
        }
        return new PrologEnglet(prolog);
    }

    public static class PrologResults {

        private String[] vars;
        private Map<String,Integer> m;
        private List<String[]> results;

        private PrologResults(SolveInfo info) throws NoSolutionException {
            List<Var> vs = info.getBindingVars();
            vars = new String[vs.size()];
            m = new HashMap<String, Integer>(vs.size());
            for (int i=0; i<vs.size(); ++i) {
                Var v = vs.get(i);
                vars[i] = v.getName();
                m.put(v.getName(), i);
            }

            this.results = new ArrayList<String[]>();
            addResults(info);
        }

        private void addResults(SolveInfo info) throws NoSolutionException {
            List<Var> vs = info.getBindingVars();
            String[] result = new String[vars.length];
            for (Var v:vs) {
                Integer i = m.get(v.getName());
                if (i==null)
                    throw new RuntimeException("Unknown variable '"+v.getName()+"' met in solution alternative: "+info);
                result[i] = v.toStringFlattened();
            }
            results.add(result);
        }

        public Integer getVariableIndex(String var) {
            return m.get(var);
        }

        public List<String[]> getResults() {
            return results;
        }

        public String[] getVariables() {
            return vars;
        }

        @Override
        public String toString() {
            StringBuffer buffer = new StringBuffer();

            for (String v:vars)
                buffer.append(v).append("\t");

            buffer.append("\n");
            buffer.append("================================\n");

            for (String[] result:results) {
                for (String v:result) {
                    buffer.append(v).append("\t");
                }
                buffer.append("\n");
            }

            return buffer.toString();
        }


    }

    public static class PrologEnglet {

        private Prolog prolog;

        private PrologEnglet(Prolog prolog) {
            this.prolog = prolog;
        }

        public PrologResults query(String question) throws MalformedGoalException, NoSolutionException {
            PrologResults prologResults = null;;

            SolveInfo si = prolog.solve(question);
            while (si!=null && si.isSuccess()) {
                if (prologResults==null)
                    prologResults = new PrologResults(si);
                else
                    prologResults.addResults(si);
                
                try {
                    si = prolog.solveNext();
                } catch (NoMoreSolutionException ex) {
                    si = null;
                }
            }
            prolog.solveEnd();

            return prologResults;
        }

    }

}
