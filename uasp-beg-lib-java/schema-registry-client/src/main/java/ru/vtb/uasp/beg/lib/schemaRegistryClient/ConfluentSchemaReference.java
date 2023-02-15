package ru.vtb.uasp.beg.lib.schemaRegistryClient;

public class ConfluentSchemaReference {
    private String name;
    private String subject;
    private Integer version;

    public String getName() {
        return name;
    }

    public ConfluentSchemaReference setName(String name) {
        this.name = name;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public ConfluentSchemaReference setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public ConfluentSchemaReference setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
