package org.lg.UUID;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class UUIDGenerator implements IdentifierGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        UUIDRepo repo=UUIDRepository.getInstance();
        return repo.generateUUID();
    }
}
