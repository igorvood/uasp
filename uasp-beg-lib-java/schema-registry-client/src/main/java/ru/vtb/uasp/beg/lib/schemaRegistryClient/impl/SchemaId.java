package ru.vtb.uasp.beg.lib.schemaRegistryClient.impl;

import java.util.Objects;

public class SchemaId {
    private final String subject;
    private final String version;

    public SchemaId(String subject, String version) {
        this.subject = subject;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchemaId schemaId = (SchemaId) o;
        return Objects.equals(subject, schemaId.subject) && Objects.equals(version, schemaId.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, version);
    }

    @Override
    public String toString() {
        return "SchemaId{" +
                "subject='" + subject + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
