package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

import model.SnakeGame;

// A* strategy
public class AStarAgentStrategy extends AgentStrategy
{
    // Value stored in a node
    private static class Value
    {
        // Last action performed
        public AgentAction lastAction;

        // New position achieved after the last action
        public Position currentPosition;

        public Value(AgentAction lastAction, Position currentPosition)
        {
            this.lastAction = lastAction;
            this.currentPosition = currentPosition;
        }

        @Override
        public String toString()
        {
            return String.format("(%s, %s)", lastAction, currentPosition);
        }
    }

    // Node stored in a search tree
    private static class Node
    {
        // Value of the node
        public Value value;

        // Local cost of the node (g)
        public int localCost;

        // Heuristic cost of the node (h)
        public int heuristicCost;

        public Node(Value value, int localCost, int heuristicCost)
        {
            this.value = value;
            this.localCost = localCost;
            this.heuristicCost = heuristicCost;
        }

        // Global cost of the node (f)
        public int globalCost()
        {
            return localCost + heuristicCost;
        }

        @Override
        public String toString()
        {
            return String.format("(v: %s, g: %s, h: %s)", value, localCost, heuristicCost);
        }
    }

    private SnakeGame snakeGame;
    private OneStepAheadAgentStrategy oneStepAheadAgentStrategy;

    public AStarAgentStrategy(Snake snake, SnakeGame snakeGame)
    {
        super(snake);
        this.snakeGame = snakeGame;
        oneStepAheadAgentStrategy = new OneStepAheadAgentStrategy(snake, snakeGame);
    }

    @Override
    public AgentAction nextAction()
    {
        //System.out.println("nextAction()");

        // Record the predecessors to return the next action
        Map<Node, Node> predecessors = new HashMap<Node, Node>();

        // Root node of the search tree
        // It has zero cost
        Value rootValue = new Value(snake.getLastAction(), snake.getPositions().get(0));
        Node rootNode = new Node(rootValue, 0, 0);

        // List of open node sorted in ascending order on global cost
        // The root is the first open node
        PriorityQueue<Node> openNodes = new PriorityQueue<Node>(1, (node1, node2) -> node1.globalCost() - node2.globalCost());
        openNodes.add(rootNode);

        // List of closed node
        List<Node> closedNodes = new ArrayList<>();

        // Save the index of the snake to be able to copy it
        int snakeIndex = snakeGame.getSnakes().indexOf(snake);

        // While there is open nodes
        while (!openNodes.isEmpty())
        {
            // Extract the first open node with the lowest global cost
            Node bestOpenNode = openNodes.poll();

            //System.out.format("   - best open node: %s\n", bestOpenNode);

            // Copy the game
            SnakeGame snakeGameCopy = null;
            try
            {

                snakeGameCopy = new SnakeGame(snakeGame);
            }
            catch (Exception exception) {}

            // Get the right state of the snake
            Snake snakeCopy = snakeGameCopy.getSnakes().get(snakeIndex);

            // Get the path of the best open node
            Stack<AgentAction> bestOpenNodePath = new Stack<AgentAction>();
            Node bestOpenNodeIterator = bestOpenNode;
            while (predecessors.containsKey(bestOpenNodeIterator))
            {
                bestOpenNodePath.push(bestOpenNodeIterator.value.lastAction);
                bestOpenNodeIterator = predecessors.get(bestOpenNodeIterator);
            }

           //System.out.format("   - best open node path : %s\n", bestOpenNodePath);

            // Get the right state of the game
            //System.out.println("   - get the right state of the game:");
            while (!bestOpenNodePath.empty())
            {
                //System.out.format("      - %s\n", bestOpenNodePath.peek());
                snakeGameCopy.testTurn(snakeCopy, bestOpenNodePath.pop());
            }

            // Find the goal position
            Position goalPosition = null;
            for (FeaturesItem featuresItem: snakeGameCopy.getFeaturesItems())
            {
                if (featuresItem.getItemType() == ItemType.APPLE)
                {
                    goalPosition = featuresItem.getPosition();
                    break;
                }
            }

            //System.out.println("   - goal position: " +  goalPosition);
            //System.out.println("   - head position: " +  snakeCopy.getPositions().get(0));

            // There is a goal
            if (goalPosition != null)
            {
                // For each successors of the best open node
                //System.out.println("   - for each successors of best open node:");
                for (AgentAction successorAction: AgentAction.values())
                {
                    //System.out.printf("      - %s: \n", successorAction);

                    // Successor is legal
                    if (snakeGameCopy.isLegalMove(snakeCopy, successorAction))
                    {
                        //System.out.printf("         - legal\n");
                        Position successorCurrentPosition = bestOpenNode.value.currentPosition.move(successorAction, snakeGameCopy.getInputMap().getSize());

                        // The goal is reached
                        if (goalPosition.equals(successorCurrentPosition))
                        {
                            //System.out.println("         - goal reached !!!");

                            // Successor has predecessors
                            if (predecessors.containsKey(bestOpenNode))
                            {
                                // Return the action of the first ancestor of the successor
                                while (predecessors.containsKey(bestOpenNode) && predecessors.containsKey(predecessors.get(bestOpenNode)))
                                    bestOpenNode = predecessors.get(bestOpenNode);
                                return bestOpenNode.value.lastAction;
                            }

                            // Return the action of the successor because it has no predecessors
                            else
                                return successorAction;
                        }

                        // The goal is not found
                        else
                        {                           
                            //System.out.println("         - goal not reached");

                            Node successorNode = null;
                            int currentLocalCost = 0;
                            boolean successorIsOpen = false;
                            boolean successorIsClosed = false;

                            // The successor node may be open
                            for (Node openNode: openNodes)
                            {
                                if (openNode.value.currentPosition.equals(successorCurrentPosition))
                                {
                                    successorNode = openNode;
                                    currentLocalCost = openNode.localCost;
                                    successorIsOpen = true;
                                    break;
                                }
                            }

                            // The successor node is maybe a closed node if is not an open
                            if (!successorIsOpen)
                            {
                                for (Node closedNode: closedNodes)
                                {
                                    if (closedNode.value.currentPosition.equals(successorCurrentPosition))
                                    {
                                        successorNode = closedNode;
                                        currentLocalCost = closedNode.localCost;
                                        successorIsClosed = true;
                                        break;
                                    }
                                }
                            }

                            // Update successor
                            int successorLocalCost = bestOpenNode.localCost + 1;
                            //if (!(successorIsClosed || (successorIsOpen && currentLocalCost <= successorLocalCost)))
                            if (!successorIsClosed && !(successorIsOpen && currentLocalCost <= successorLocalCost))
                            {
                                //System.out.println("         - update successor");

                                // Create the successor node if it does not exists
                                if (successorNode == null)
                                {
                                    // Create the successor node
                                    Value successorValue = new Value(successorAction, successorCurrentPosition);
                                    int successorHeuristicCost = successorCurrentPosition.manhatanDistance(goalPosition);
                                    successorNode = new Node(successorValue, successorLocalCost, successorHeuristicCost);
                                }

                                // Update the cost of the exiting successor node
                                else
                                    successorNode.localCost = successorLocalCost;

                                // Record predecessors
                                predecessors.put(successorNode, bestOpenNode);

                                // Open the successor node
                                if (!successorIsOpen)
                                    openNodes.add(successorNode);
                            }
                        }
                    }

                    // Successor is illegal
                    //else
                        //System.out.printf("         - illegal\n");
                }
            }

            // Close the best open node
            closedNodes.add(bestOpenNode);
        }

        // If no goal is found, play with one step ahead lookup
        //System.out.println("   - Error: goal not found !!!");
        //try { Thread.sleep(100000000); } catch (InterruptedException e) { e.printStackTrace(); }
        return oneStepAheadAgentStrategy.nextAction();
    }
}
