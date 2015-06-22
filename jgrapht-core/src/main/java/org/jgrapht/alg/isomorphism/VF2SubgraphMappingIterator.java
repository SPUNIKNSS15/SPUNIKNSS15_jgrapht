package org.jgrapht.alg.isomorphism;

import java.util.Comparator;


public class VF2SubgraphMappingIterator<V,E>
    extends VF2MappingIterator<V,E>
{

    public VF2SubgraphMappingIterator(
                    GraphOrdering<V,E> ordering1,
                    GraphOrdering<V,E> ordering2,
                    Comparator<V> vertexComparator,
                    Comparator<E> edgeComparator)
    {
        super(ordering1, ordering2, vertexComparator, edgeComparator);
    }


    @Override
    protected IsomorphicGraphMapping<V, E> match() {
        VF2State<V, E> s;

        if (stateStack.isEmpty()) {
            s = new VF2SubgraphIsomorphismState<V, E>(ordering1, ordering2,
                            vertexComparator, edgeComparator);
            if (s.n1<s.n2) return null;

            if (ordering2.getGraph().vertexSet().isEmpty())
                return hadOneMapping != null ? null : s.getCurrentMapping();
        } else {
            stateStack.pop().backtrack();
            s = stateStack.pop();
        }


        while (true) {
            while (s.nextPair()) {
                if (s.isFeasiblePair()) {
                    stateStack.push(s);
                    s = new VF2SubgraphIsomorphismState<V,E>(s);
                    s.addPair();

                    if (s.isGoal()) {
                        stateStack.push(s);
                        return s.getCurrentMapping();
                    }

                    s.resetAddVertexes();
                }
            }

            if (stateStack.isEmpty())
                return null;

            s.backtrack();
            s = stateStack.pop();
        }
    }

}
