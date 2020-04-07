package by.azgaar.storage.enumeration;

public enum LoginPath {

    GITHUB("github"),
    GOOGLE("google"),
    FACEBOOK("facebook");

    private String value;

    LoginPath(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}