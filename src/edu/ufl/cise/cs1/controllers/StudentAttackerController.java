package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.util.ArrayList;
import java.util.List;

public final class StudentAttackerController implements AttackerController
{
	public void init(Game game) { }

	public void shutdown(Game game) { }

	private enum State { EAT_PILL, EAT_GHOST, EAT_POWER_PILL }

	private State state;

	private Game game;

	public int update(Game game,long timeDue)
	{
		this.game = game.copy();
		Integer action = null;
		while (action == null) {
			state = getState();
			switch (state) {
				case EAT_POWER_PILL:
					action = eatPowerPill();
					break;
				case EAT_GHOST:
					action = eatGhost();
					break;
				case EAT_PILL:
					action = eatPill();
			}
		}
		return action;
	}

	private State getState() {
		if (getEdibleGhosts().size() > 0) {
			return State.EAT_GHOST;
		}
		if (game.getPowerPillList().size() > 0) {
			return State.EAT_POWER_PILL;
		}
		return State.EAT_PILL;
	}

	private int eatPill() {
		List<Node> pillList = game.getPillList();
		return goToNearest(pillList);
	}

	private int eatGhost() {
		List<Node> defenderNodes = new ArrayList<>();
		for (Defender d : getEdibleGhosts()) {
			defenderNodes.add(d.getLocation());
		}
		return goToNearest(defenderNodes);
	}

	private Integer eatPowerPill() {
		List<Node> pillList = game.getPowerPillList();
		return goToNearest(pillList);
	}


	private List<Defender> getEdibleGhosts() {
		List<Defender> defenderList = game.getDefenders();
		List<Defender> edibleGhost = new ArrayList<>();
		for (Defender d : defenderList) {
			if (d.isVulnerable()) {
				edibleGhost.add(d);
			}
		}
		return edibleGhost;
	}

	private Integer goToNearest(List<Node> nodeList) {
		if (nodeList.size() == 0) {
			return null;
		}
		Node nearestNode = null;
		for (Node n : nodeList) {
			if (nearestNode == null) {
				nearestNode = n;
				continue;
			}
			Node nextPill = n;
			if (distanceTo(nextPill) < distanceTo(nearestNode)) {
				nearestNode = nextPill;
			}
		}
		return game.getAttacker().getNextDir(nearestNode, true);
	}

	private int distanceTo(Node node) {
		return node.getPathDistance(game.getAttacker().getLocation());
	}

}