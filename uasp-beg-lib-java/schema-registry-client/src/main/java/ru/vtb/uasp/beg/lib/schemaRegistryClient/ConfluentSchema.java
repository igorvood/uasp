package ru.vtb.uasp.beg.lib.schemaRegistryClient;

import java.util.List;

public class ConfluentSchema {
    private String subject;
    private int version;
    private long id;
    private String schemaType;
    private List<ConfluentSchemaReference> references;
    private String schema;

    public String getSubject() {
        return subject;
    }

    public ConfluentSchema setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public ConfluentSchema setVersion(int version) {
        this.version = version;
        return this;
    }

    public long getId() {
        return id;
    }

    public ConfluentSchema setId(long id) {
        this.id = id;
        return this;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public ConfluentSchema setSchemaType(String schemaType) {
        this.schemaType = schemaType;
        return this;
    }

    public List<ConfluentSchemaReference> getReferences() {
        return references;
    }

    public ConfluentSchema setReferences(List<ConfluentSchemaReference> references) {
        this.references = references;
        return this;
    }

    public String getSchema() {
        return schema;
    }

    public ConfluentSchema setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    @Override
    public String toString() {
        return "ConfluentSchema{" +
                "subject='" + subject + '\'' +
                ", version=" + version +
                ", id=" + id +
                ", schemaType='" + schemaType + '\'' +
                ", references=" + references +
                ", schema='" + schema + '\'' +
                '}';
    }
}