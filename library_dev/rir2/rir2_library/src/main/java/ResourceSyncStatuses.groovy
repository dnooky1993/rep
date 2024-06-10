enum ResourceSyncStatuses {
    OK ("OK"),
    PROCESSING ("PROCESSING"),
    IN_PROGRESS ("IN_PROGRESS"),
    ERROR ("ERROR")

    private final String name;

    private ResourceSyncStatuses(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}