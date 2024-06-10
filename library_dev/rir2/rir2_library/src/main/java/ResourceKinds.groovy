enum ResourceKinds {
    SAR ("SPECIAL_AVAILABLE_RESOURCE"),
    AR ("AVAILABLE_RESOURCE")

    private final String name;

    private ResourceKinds(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}