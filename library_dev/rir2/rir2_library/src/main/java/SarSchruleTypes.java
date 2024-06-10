public enum SarSchruleTypes {
    WORK_TIME_DEFINITION ("sarSchruleWTTypeId", 10),
    REJECTION ("sarSchruleRejectionTypeId",2),
    TREATMENT_REDEFINITION ("sarSchruleTRTypeId",7),
    ROUTE_REDEFINITION ("sarSchruleRRTypeId",9),
    AGE_GROUP_RESTRICTION ("sarSchruleAGTypeId",8);

    private final String propertyName;
    private final int priority;

    private SarSchruleTypes(String propertyName, int priority) {
        this.propertyName = propertyName;
        this.priority = priority;
    }

    public String getPropertyName() {
        return this.propertyName;
    }
    public int getPriority() {
        return this.priority;
    }
}