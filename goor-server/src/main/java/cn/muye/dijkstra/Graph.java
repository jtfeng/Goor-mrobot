package cn.muye.dijkstra;

import java.util.*;

class Graph {

    private final Map<Long, List<Vertex>> vertices;

    public Graph() {
        this.vertices = new HashMap<Long, List<Vertex>>();
    }


    public void addVertex(Long id, List<Vertex> vertex) {
        this.vertices.put(id, vertex);
    }

    public List<Long> getShortestPath(Long start, Long finish) {
        final Map<Long, Integer> distances = new HashMap<Long, Integer>();
        final Map<Long, Vertex> previous = new HashMap<Long, Vertex>();
        PriorityQueue<Vertex> nodes = new PriorityQueue<Vertex>();

        for(Long vertex : vertices.keySet()) {
            if (vertex.longValue() == start.longValue()) {
                start = vertex;
                distances.put(vertex, 0);
                nodes.add(new Vertex(vertex, 0));
            } else {
                distances.put(vertex, Integer.MAX_VALUE);
                nodes.add(new Vertex(vertex, Integer.MAX_VALUE));
            }
            previous.put(vertex, null);
        }

        while (!nodes.isEmpty()) {
            Vertex smallest = nodes.poll();
            if (smallest.getId().longValue() == finish.longValue()) {
                final List<Long> path = new ArrayList<Long>();
                while (previous.get(smallest.getId()) != null) {
                    path.add(smallest.getId());
                    smallest = previous.get(smallest.getId());
                }
                path.add(start);
                Collections.reverse(path);
                return path;
            }

            if (distances.get(smallest.getId()) == Integer.MAX_VALUE) {
                break;
            }

            for (Vertex neighbor : vertices.get(smallest.getId())) {
                Integer alt = distances.get(smallest.getId()) + neighbor.getDistance();
                if (alt < distances.get(neighbor.getId())) {
                    distances.put(neighbor.getId(), alt);
                    previous.put(neighbor.getId(), smallest);

                    forloop:
                    for(Vertex n : nodes) {
                        if (n.getId().longValue() == neighbor.getId().longValue()) {
                            nodes.remove(n);
                            n.setDistance(alt);
                            nodes.add(n);
                            break forloop;
                        }
                    }
                }
            }
        }

        return new ArrayList<Long>();
    }

}
