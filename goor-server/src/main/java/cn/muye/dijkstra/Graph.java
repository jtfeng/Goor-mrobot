package cn.muye.dijkstra;

import java.util.*;

public class Graph {

    private final Map<Long, List<Vertex>> vertices;

    public Graph() {
        this.vertices = new HashMap<Long, List<Vertex>>();
    }


    public void addVertex(Long id, List<Vertex> vertex) {
        this.vertices.put(id, vertex);
    }

    public RoadPathResult getShortestPath(Long start, Long finish) {
        RoadPathResult roadPathResult = new RoadPathResult();
        Long totalWeight = 0L;
        final Map<Long, Long> distances = new HashMap<>();
        final Map<Long, Vertex> previous = new HashMap<Long, Vertex>();
        PriorityQueue<Vertex> nodes = new PriorityQueue<Vertex>();

        for(Long vertex : vertices.keySet()) {
            if (vertex .equals(start)) {
                start = vertex;
                distances.put(vertex, 0L);
                nodes.add(new Vertex(vertex, 0L));
            } else {
                distances.put(vertex, Long.MAX_VALUE);
                nodes.add(new Vertex(vertex, Long.MAX_VALUE));
            }
            previous.put(vertex, null);
        }

        while (!nodes.isEmpty()) {
            Vertex smallest = nodes.poll();
            if (smallest.getId().equals(finish)) {
                final List<Long> path = new ArrayList<Long>();
                while (previous.get(smallest.getId()) != null) {
                    path.add(smallest.getId());
                    smallest = previous.get(smallest.getId());
                }
                path.add(start);
                Collections.reverse(path);
                roadPathResult.setPointIds(path);
                roadPathResult.setTotalWeight(totalWeight);
                return roadPathResult;
            }

            if (distances.get(smallest.getId()).equals(Long.MAX_VALUE)) {
                break;
            }

            for (Vertex neighbor : vertices.get(smallest.getId())) {
                Long alt = distances.get(smallest.getId()) + neighbor.getDistance();
                if (alt < distances.get(neighbor.getId())) {
                    distances.put(neighbor.getId(), alt);
                    previous.put(neighbor.getId(), smallest);

                    forloop:
                    for(Vertex n : nodes) {
                        if (n.getId().equals(neighbor.getId())) {
                            nodes.remove(n);
                            n.setDistance(alt);
                            nodes.add(n);
                            totalWeight = alt;
                            break forloop;
                        }
                    }
                }
            }
        }

        return roadPathResult;
    }
}
