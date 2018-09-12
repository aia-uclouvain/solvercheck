package be.uclouvain.solvercheck.core.data;

import java.util.List;

/**
 * An assignment is a complete mapping from variables to values.
 * In this context, we consider variables to be identified by an integer key and values to always be from type integer.
 */
public interface Assignment extends List<Integer> { }
