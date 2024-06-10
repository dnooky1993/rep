public enum ArSchruleTypes {
    WORK_TIME_DEFINITION ("arSchruleWTTypeId", 10),
    REJECTION ("arSchruleRejectionTypeId",2),
    TREATMENT_REDEFINITION ("arSchruleTRTypeId",7),
    ROUTE_REDEFINITION ("arSchruleRRTypeId",9),
    AGE_GROUP_RESTRICTION ("arSchruleAGTypeId",8);

    private final String propertyName;
    private final int priority;

    private ArSchruleTypes(String propertyName, int priority) {
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
