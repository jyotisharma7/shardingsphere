/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.encrypt.metadata.reviser;

import org.apache.shardingsphere.encrypt.rule.EncryptRule;
import org.apache.shardingsphere.encrypt.rule.EncryptTable;
import org.apache.shardingsphere.infra.metadata.database.schema.decorator.reviser.column.ColumnNameReviser;

import java.util.Collection;
import java.util.Optional;

/**
 * Encrypt column name reviser.
 */
public final class EncryptColumnNameReviser implements ColumnNameReviser<EncryptRule> {
    
    private final EncryptTable encryptTable;
    
    private final Collection<String> plainColumns;
    
    private final Collection<String> assistedQueryColumns;
    
    private final Collection<String> likeQueryColumns;
    
    public EncryptColumnNameReviser(final EncryptTable encryptTable) {
        this.encryptTable = encryptTable;
        plainColumns = encryptTable.getPlainColumns();
        assistedQueryColumns = encryptTable.getAssistedQueryColumns();
        likeQueryColumns = encryptTable.getLikeQueryColumns();
    }
    
    @Override
    public Optional<String> revise(final String originalName, final String tableName, final EncryptRule rule) {
        if (plainColumns.contains(originalName)) {
            return Optional.of(encryptTable.getLogicColumnByPlainColumn(originalName));
        }
        if (encryptTable.isCipherColumn(originalName)) {
            return Optional.of(encryptTable.getLogicColumnByCipherColumn(originalName));
        }
        if (!assistedQueryColumns.contains(originalName) && !likeQueryColumns.contains(originalName)) {
            return Optional.of(originalName);
        }
        return Optional.empty();
    }
}
