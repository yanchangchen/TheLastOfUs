package app;

public interface AppConstants
{
    public static final String JSON_DIR = System.getenv("C_JSON_DIR");
    public static final String MONGO_HOST = System.getenv("C_MONGO_HOST");
    public static final String MONGO_PORT = System.getenv("C_MONGO_PORT");
    public static final String MONGO_DB = System.getenv("C_MONGO_DB");
    public static final String MONGO_COLLECTION = System.getenv("C_MONGO_COLLECTION");
}
