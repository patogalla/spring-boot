package com.patogalla.api;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;


public class CustomHSQLDialect extends HSQLDialect {

    @Override
    public boolean dropConstraints() {
        // We don't need to drop constraints before dropping tables, that just
        // leads to error messages about missing tables when we don't have a
        // schema in the database
        return false;
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
        typeContributions.contributeType(new UUIDStringCustomType());
    }

    public class UUIDStringCustomType extends AbstractSingleColumnStandardBasicType {

        public UUIDStringCustomType() {
            super(VarcharTypeDescriptor.INSTANCE, UUIDTypeDescriptor.INSTANCE);
        }

        @Override
        public String getName() {
            return "pg-uuid";
        }

    }
}