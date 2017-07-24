package teabagml.datasets;

public interface Queryable {
    /**
     * @return the arity of the dataset
     */
    public Arity getArity();

    /**
     * @return the dimension of the dataset
     */
    public int getDimension();

    /**
     * @return the size of the dataset
     */
    public int getDataSize();

    /**
     * @return true if the dataset is symbolic, otherwise fasle
     */
    public boolean isSymbolic();

    /**
     * @return true if the dataset is labelled, otherwise fasle
     */
    public boolean isLabelled();

    /**
     * @param query the query like (1, 3, -1, 5) where -1 represents ANY
     * @return the number of records which match the query, return -1 if the query is invalid
     */
    public int count(int[] query);
}
