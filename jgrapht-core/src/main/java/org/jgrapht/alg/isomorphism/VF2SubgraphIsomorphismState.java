/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
/* -------------------------
 * VF2SubgraphIsomorphismState.java
 * -------------------------
 * (C) Copyright 2015, by Fabian Späh and Rita Dobler.
 *
 * Author:  Fabian Späh, Rita Dobler
 *
 * $Id$
 *
 * Changes
 * -------
 * 20-Jun-2015 : Initial revision (FS);
 *
 */
package org.jgrapht.alg.isomorphism;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class VF2SubgraphIsomorphismState<V,E>
    extends VF2State<V,E>
{

    public VF2SubgraphIsomorphismState(
                    GraphOrdering<V,E> g1,
                    GraphOrdering<V,E> g2,
                    Comparator<V> vertexComparator,
                    Comparator<E> edgeComparator) {
        super(g1, g2, vertexComparator, edgeComparator);
    }

    public VF2SubgraphIsomorphismState(VF2State<V,E> s) {
        super(s);
    }


    /**
     * @return true, if the already matched vertices of graph1 plus the first
     * vertex of nextPair are subgraph isomorphic to the already matched
     * vertices of graph2 and the second one vertex of nextPair.
     */
    @Override
    public boolean isFeasiblePair() {
        String pairstr  = "(" + g1.getVertex(addVertex1) + ", " +
                                g2.getVertex(addVertex2) + ")",
               abortmsg = pairstr + " does not fit in the current matching";

        // check for semantic equality of both vertexes
        if (!areCompatibleVertexes(addVertex1, addVertex2))
            return false;

        int termOutPred1 = 0,
            termOutPred2 = 0,
            termInPred1  = 0,
            termInPred2  = 0,
            newPred1     = 0,
            newPred2     = 0,
            termOutSucc1 = 0,
            termOutSucc2 = 0,
            termInSucc1  = 0,
            termInSucc2  = 0,
            newSucc1     = 0,
            newSucc2     = 0;

        nextCandFrom=Candidates.ALL;
        cand1=new LinkedList<Integer>();
        cand2=NULL_NODE;

        // check outgoing edges of addVertex2
        for (int other2 : g2.getOutEdges(addVertex2)) {
            if (core2[other2] != NULL_NODE) {
                int other1 = core2[other2];
                if (!g1.hasEdge(addVertex1, other1))    {
                    showLog("isFeasbilePair", abortmsg + ": edge from " +
                                    g1.getVertex(addVertex1) + " to " +
                                    g1.getVertex(other1) +
                                    " is missing in the 1st graph");
                    return false;
                }
            } else {
                if (in2[other2] > 0)
                    termInSucc2++;
                if (out2[other2] > 0)
                    termOutSucc2++;
                if (in2[other2] == 0 && out2[other2] == 0)
                    newSucc2++;
            }
        }

        // check outgoing edges of addVertex1
        for (int other1 : g1.getOutEdges(addVertex1)) {
            if (core1[other1] != NULL_NODE) {
                int other2 = core1[other1];
                if (!g2.hasEdge(addVertex2, other2) ||
                        !areCompatibleEdges(addVertex1, other1,
                                addVertex2, other2))    {
                    showLog("isFeasiblePair", abortmsg + ": edge from " +
                            g2.getVertex(addVertex2) + " to " +
                            g2.getVertex(other2) +
                            " is missing in the 2nd graph");
                    return false;
                }
            } else {
                if (in1[other1] > 0)
                    termInSucc1++;
                if (out1[other1] > 0)
                    termOutSucc1++;
                if (in1[other1] == 0 && out1[other1] == 0)
                    newSucc1++;
            }
        }

        /* negated feasibility condition */
        if (termInSucc1 < termInSucc2 ||
            termOutSucc1 < termOutSucc2 ||
            newSucc1 < newSucc2)
        {
            if (DEBUG)  {
                String cause = "",
                          v1 = g1.getVertex(addVertex1).toString(),
                          v2 = g2.getVertex(addVertex2).toString();

                if (termInSucc2 > termInSucc1)
                    cause = "|Tin2 ∩ Succ(Graph2, " + v2 +
                        ")| > |Tin1 ∩ Succ(Graph1, " + v1 + ")|";
                else if (termOutSucc2 > termOutSucc1)
                    cause = "|Tout2 ∩ Succ(Graph2, " + v2 +
                        ")| > |Tout1 ∩ Succ(Graph1, " + v1 + ")|";
                else if (newSucc2 > newSucc1)
                    cause = "|N‾ ∩ Succ(Graph2, " + v2 +
                        ")| > |N‾ ∩ Succ(Graph1, " + v1 + ")|";

                showLog("isFeasbilePair", abortmsg + ": " + cause);
            }

            return false;
        }

        // check incoming edges of addVertex2
        for (int other2 : g2.getInEdges(addVertex2)) {
            if (core2[other2] != NULL_NODE) {
                int other1 = core2[other2];
                if (!g1.hasEdge(other1, addVertex1))    {
                    showLog("isFeasiblePair", abortmsg + ": edge from " +
                                    g1.getVertex(other1) + " to " +
                                    g1.getVertex(addVertex1) +
                                    " is missing in the 1st graph");
                    return false;
                }
            } else {
                if (in2[other2] > 0)
                    termInPred2++;
                if (out2[other2] > 0)
                    termOutPred2++;
                if (in2[other2] == 0 && out2[other2] == 0)
                    newPred2++;
            }
        }

        // check incoming edges of addVertex1
        for (int other1 : g1.getInEdges(addVertex1)) {
            if (core1[other1] != NULL_NODE) {
                int other2 = core1[other1];
                if (!g2.hasEdge(other2, addVertex2) ||
                        !areCompatibleEdges(other1, addVertex1,
                                other2, addVertex2)) {
                    showLog("isFeasbilePair", abortmsg + ": edge from " +
                            g2.getVertex(other2) + " to " +
                            g2.getVertex(addVertex2) +
                            " is missing in the 2nd graph");
                    return false;
                }
            } else {
                if (in1[other1] > 0)
                    termInPred1++;
                if (out1[other1] > 0)
                    termOutPred1++;
                if (in1[other1] == 0 && out1[other1] == 0)
                    newPred1++;
            }
        }

        /* feasibility condition */
        if (termInPred1 >= termInPred2 &&
                termOutPred1 >= termOutPred2 &&
                newPred1 >= newPred2)
        {
            /* search smallest possible candidate set for next iteration */
            Integer minimalNumber = 0;
            if (termInPred2 > 0) {
                nextCandFrom  = Candidates.INPRED;
                minimalNumber = termInPred1;
            }
            if (termInSucc2 > 0 && (minimalNumber == 0 || termInSucc1 < minimalNumber)) {
                nextCandFrom  = Candidates.INSUCC;
                minimalNumber = termInSucc1;
            }
            if (termOutPred2 > 0 && (minimalNumber == 0 || termOutPred1 < minimalNumber)) {
                nextCandFrom  = Candidates.OUTPRED;
                minimalNumber = termOutPred1;
            }
            if (termOutSucc2 > 0 && (minimalNumber == 0 || termOutSucc1 < minimalNumber)) {
                nextCandFrom  = Candidates.OUTSUCC;
                minimalNumber = termOutSucc1;
            }
            if (newSucc2 > 0 && (minimalNumber == 0 || newSucc1 < minimalNumber)) {
                nextCandFrom  = Candidates.NEWSUCC;
                minimalNumber = newSucc1;
            }
            if (newPred2 > 0 && (minimalNumber == 0 || newPred1 < minimalNumber)) {
                nextCandFrom  = Candidates.NEWPRED;
                minimalNumber = newPred1;
            }

            /* get candidates for next step if optimal candidate set relies to insucc */
            if (nextCandFrom==Candidates.INSUCC) {
                for (int other2 : g2.getOutEdges(addVertex2)) {// check outgoing edges of addVertex2
                    if (core2[other2] == NULL_NODE) {
                        if (in2[other2] > 0) {// get first possible vertex counting for termInSucc2 as G2 candidate
                            cand2 = other2;
                            break;
                        }
                    }
                }
                for (int other1 : g1.getOutEdges(addVertex1)) {// check outgoing edges of addVertex1
                    if (core1[other1] == NULL_NODE) {
                        if (in1[other1] > 0)
                            cand1.add(other1);// get all possible vertices counting for termInSucc1 as G1 candidates
                    }
                }
            }

            /* get candidates for next step if optimal candidate set relies to outsucc */
            if (nextCandFrom == Candidates.OUTSUCC) {
                for (int other2 : g2.getOutEdges(addVertex2)) {// check outgoing edges of addVertex2
                    if (core2[other2] == NULL_NODE) {
                        if (out2[other2] > 0) {// get first possible vertex counting for termOutSucc2 as G2 candidate
                            cand2 = other2;
                            break;
                        }
                    }
                }
                for (int other1 : g1.getOutEdges(addVertex1)) {// check outgoing edges of addVertex1
                    if (core1[other1] == NULL_NODE) {
                        if (out1[other1] > 0)
                            cand1.add(other1);// get all possible vertices counting for termOutSucc1 as G1 candidates
                    }
                }
            }

            /* get candidates for next step if optimal candidate set relies to inpred */
            if (nextCandFrom==Candidates.INPRED) {
                for (int other2 : g2.getInEdges(addVertex2)) {// check incoming edges of addVertex2
                    if (core2[other2] == NULL_NODE) {
                        if (in2[other2] > 0) {// get first possible vertex counting for termInPred2 as G2 candidate
                            cand2 = other2;
                            break;
                        }
                    }
                }
                for (int other1 : g1.getInEdges(addVertex1)) {// check incoming edges of addVertex1
                    if (core1[other1] == NULL_NODE) {
                        if (in1[other1] > 0)
                            cand1.add(other1);// get all possible vertices counting for termInPred1 as G1 candidates
                    }
                }
            }

            /* get candidates for next step if optimal candidate set relies to outpred */
            if (nextCandFrom == Candidates.OUTPRED) {
                for (int other2 : g2.getInEdges(addVertex2)) {// check incoming edges of addVertex2
                    if (core2[other2] == NULL_NODE) {
                        if (out2[other2] > 0) {// get first possible vertex counting for termOutPred2 as G2 candidate
                            cand2 = other2;
                            break;
                        }
                    }
                }
                for (int other1 : g1.getInEdges(addVertex1)) {// check incoming edges of addVertex1
                    if (core1[other1] == NULL_NODE) {
                        if (out1[other1] > 0)
                            cand1.add(other1);// get all possible vertices counting for termOutPred1 as G1 candidates
                    }
                }
            }

            /* get candidates for next step if optimal candidate set relies to newsucc */
            if (nextCandFrom==Candidates.NEWSUCC) {
                for (int other2 : g2.getOutEdges(addVertex2)) {// check outgoing edges of addVertex2
                    if (core2[other2] == NULL_NODE) {
                        if (in2[other2] == 0 && out2[other2]== 0) {
                            cand2 = other2;// get first possible vertex counting for newSucc2 as G2 candidate
                            break;
                        }
                    }
                }
                for (int other1 : g1.getOutEdges(addVertex1)) {// check outgoing edges of addVertex1
                    if (core1[other1] == NULL_NODE) {
                        if (in1[other1] == 0 && out1[other1]== 0)
                            cand1.add(other1);// get all possible vertices counting for newSucc1 as G1 candidates
                    }
                }
            }

            /* get candidates for next step if optimal candidate set relies to newpred */
            if (nextCandFrom == Candidates.NEWPRED) {
                for (int other2 : g2.getInEdges(addVertex2)) {// check ingoing edges of addVertex2
                    if (core2[other2] == NULL_NODE) {
                        if (in2[other2] == 0 && out2[other2]== 0) {
                            cand2 = other2;// get first possible vertex counting for newPred2 as G2 candidate
                            break;
                        }
                    }
                }
                for (int other1 : g1.getInEdges(addVertex1)) {// check ingoing edges of addVertex1
                    if (core1[other1] == NULL_NODE) {
                        if (in1[other1] == 0 && out1[other1]== 0)
                            cand1.add(other1);// get all possible vertices counting for newPred1 as G1 candidates
                    }
                }
            }

            showLog("isFeasiblePair", pairstr + " fits");
            return true;
        }
        else
        {
            if (DEBUG)  {
                String cause = "",
                        v1 = g1.getVertex(addVertex1).toString(),
                        v2 = g2.getVertex(addVertex2).toString();

                if (termInPred2 > termInPred1)
                    cause = "|Tin2 ∩ Pred(Graph2, " + v2 +
                            ")| > |Tin1 ∩ Pred(Graph1, " + v1 + ")|";
                else if (termOutPred2 > termOutPred1)
                    cause = "|Tout2 ∩ Pred(Graph2, " + v2 +
                            ")| > |Tout1 ∩ Pred(Graph1, " + v1 + ")|";
                else if (newPred2 > newPred1)
                    cause = "|N‾ ∩ Pred(Graph2, " + v2 +
                            ")| > |N‾ ∩ Pred(Graph1, " + v1 + ")|";

                showLog("isFeasbilePair", abortmsg + ": " + cause);
            }

            return false;
        }
    }
}
