package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public final class StudentAttackerController implements AttackerController
{
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public Node getClosest (List<Node> list, Node gatorNode) // get node closest to gator in list of nodes
	{
		int minimum = 57005, distance; // minimum distance and distance from attacker variables
		Node closest = null;		// closest node variable

		for (Node temp : list) // temporary node objects for comparison
		{
			distance = gatorNode.getPathDistance(temp);  // distance from gator to temp node object
			if (distance < minimum)        // if distance from gator to list node is less than the minimum
			{
				minimum = distance;
				closest = temp;			// update closest node and distance
			}
		}
		return closest;
	}

	public int update(Game game,long timeDue)
	{
		Attacker gator = game.getAttacker();		// attacker object
		Node gatorNode = gator.getLocation();		// stores location of attacker
		ArrayList<Node> vuln = new ArrayList<>();		// stores location of vulnerable defenders
		for (Defender enemy : game.getDefenders())		// populates vulnerable defenders array list
		{
			if (enemy.isVulnerable())	// if enemy is vulnerable
				vuln.add(enemy.getLocation());	// location of vulnerable defender added to vuln ArrayList
		}

		Node enemyNode;	// stores location of defenders
		int action = gator.getNextDir(getClosest(game.getPillList(), gatorNode), true);	// default eat pills
		int enemyDistance, closeEnough = 4; // distance from gator to enemy, arbitrary close enough variable

		if (game.getPowerPillList().size() > 0 && vuln.size() == 0)	// if there are power pills on the map and no vulnerable enemies
		{
			if (gatorNode.getPathDistance(getClosest(game.getPowerPillList(), gatorNode)) <= // if there is a power pill
					gatorNode.getPathDistance(getClosest(game.getPillList(), gatorNode)))	 // closer to the gator than reg pill
				action = gator.getNextDir(getClosest(game.getPowerPillList(), gatorNode), true); // eat power pill
		}

		if (vuln.size() >= 1)	// if there are vulnerable enemies
		{
			action = gator.getNextDir(getClosest(vuln, gatorNode), true);	// approach nearest vulnerable enemy
		}

		else if (vuln.size() == 0)	// if there are no vulnerable enemies
		{
			for (Defender enemy : game.getDefenders()) // iterates through enemy list
			{
				enemyNode = enemy.getLocation();        // check each enemy's location
				enemyDistance = gatorNode.getPathDistance(enemyNode);    // find distance between enemy and gator

				if (enemyDistance <= closeEnough)    // if enemy is within arbitrary distance and is not vulnerable
				{
					closeEnough = enemyDistance;	// set new closeEnough value
					action = gator.getNextDir(enemyNode, false); // flee enemy so gator doesn't get eaten up!!!
				}
			}
		}

		return action;	// return action stored
	}
}
