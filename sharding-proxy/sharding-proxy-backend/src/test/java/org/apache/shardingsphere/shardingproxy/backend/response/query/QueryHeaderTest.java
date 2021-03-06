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

package org.apache.shardingsphere.shardingproxy.backend.response.query;

import lombok.SneakyThrows;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.shardingproxy.backend.schema.impl.ShardingSchema;
import org.apache.shardingsphere.spi.database.metadata.DataSourceMetaData;
import org.apache.shardingsphere.sql.parser.binder.segment.select.projection.ProjectionsContext;
import org.apache.shardingsphere.sql.parser.binder.segment.select.projection.impl.ColumnProjection;
import org.apache.shardingsphere.sql.parser.binder.segment.select.projection.impl.ExpressionProjection;
import org.apache.shardingsphere.underlying.common.metadata.ShardingSphereMetaData;
import org.apache.shardingsphere.underlying.common.metadata.column.ColumnMetaData;
import org.apache.shardingsphere.underlying.common.metadata.datasource.DataSourceMetas;
import org.apache.shardingsphere.underlying.common.metadata.table.TableMetaData;
import org.apache.shardingsphere.underlying.common.metadata.table.TableMetas;
import org.junit.Test;

import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class QueryHeaderTest {
    
    @Test
    public void assertQueryHeaderSchema() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertThat(header.getSchema(), is("sharding_schema"));
    }
    
    @Test
    public void assertQueryHeaderTable() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertThat(header.getTable(), is("t_logic_order"));
    }
    
    @Test
    public void assertQueryHeaderColumnLabel() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertThat(header.getColumnLabel(), is("order_id"));
    }
    
    @Test
    public void assertQueryHeaderColumnNameWithoutProjectionsContext() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertThat(header.getColumnName(), is("order_id"));
    }
    
    @Test
    public void assertQueryHeaderColumnNameFromProjectionsContext() throws Exception {
        QueryHeader header = new QueryHeader(createProjectionsContext(), createResultSetMetaData(), getShardingSchema(), 1);
        assertThat(header.getColumnName(), is("order_id"));
    }
    
    @Test
    public void assertQueryHeaderColumnNameFromMetaData() throws Exception {
        QueryHeader header = new QueryHeader(createProjectionsContext(), createResultSetMetaData(), getShardingSchema(), 2);
        assertThat(header.getColumnName(), is("expr"));
    }
    
    @Test
    public void assertQueryHeaderColumnLength() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertThat(header.getColumnLength(), is(1));
    }
    
    @Test
    public void assertQueryHeaderColumnType() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertThat(header.getColumnType(), is(Types.INTEGER));
    }
    
    @Test
    public void assertQueryHeaderDecimals() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertThat(header.getDecimals(), is(1));
    }
    
    @Test
    public void assertQueryHeaderSigned() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertTrue(header.isSigned());
    }
    
    @Test
    public void assertQueryHeaderPrimaryKey() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertTrue(header.isPrimaryKey());
    }
    
    @Test
    public void assertQueryHeaderNotNull() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertTrue(header.isNotNull());
    }
    
    @Test
    public void assertQueryHeaderAutoIncrement() throws Exception {
        QueryHeader header = new QueryHeader(createResultSetMetaData(), getShardingSchema(), 1);
        assertTrue(header.isAutoIncrement());
    }
    
    @SneakyThrows
    private ShardingSchema getShardingSchema() {
        ShardingSchema result = mock(ShardingSchema.class);
        ColumnMetaData columnMetaData = new ColumnMetaData("order_id", "int", true);
        TableMetas tableMetas = mock(TableMetas.class);
        when(tableMetas.get("t_logic_order")).thenReturn(new TableMetaData(Collections.singletonList(columnMetaData), Collections.singletonList("order_id")));
        ShardingSphereMetaData metaData = mock(ShardingSphereMetaData.class);
        when(metaData.getTables()).thenReturn(tableMetas);
        DataSourceMetas dataSourceMetas = mock(DataSourceMetas.class);
        when(dataSourceMetas.getDataSourceMetaData("ds_0")).thenReturn(mock(DataSourceMetaData.class));
        when(metaData.getDataSources()).thenReturn(dataSourceMetas);
        when(result.getMetaData()).thenReturn(metaData);
        ShardingRule shardingRule = mock(ShardingRule.class);
        when(shardingRule.getLogicTableNames("t_order")).thenReturn(Collections.singletonList("t_logic_order"));
        when(result.getShardingRule()).thenReturn(shardingRule);
        when(result.getName()).thenReturn("sharding_schema");
        return result;
    }
    
    private ProjectionsContext createProjectionsContext() {
        return new ProjectionsContext(0, 0, false, Arrays.asList(new ColumnProjection("o", "order_id", "id"), new ExpressionProjection("o.order_id + 1", "expr")));
    }
    
    @SneakyThrows
    private ResultSetMetaData createResultSetMetaData() {
        ResultSetMetaData result = mock(ResultSetMetaData.class);
        when(result.getTableName(1)).thenReturn("t_order");
        when(result.getColumnLabel(1)).thenReturn("order_id");
        when(result.getColumnName(1)).thenReturn("order_id");
        when(result.getColumnName(2)).thenReturn("expr");
        when(result.getColumnType(1)).thenReturn(Types.INTEGER);
        when(result.isSigned(1)).thenReturn(true);
        when(result.isAutoIncrement(1)).thenReturn(true);
        when(result.isNullable(1)).thenReturn(ResultSetMetaData.columnNoNulls);
        when(result.getColumnDisplaySize(1)).thenReturn(1);
        when(result.getScale(1)).thenReturn(1);
        return result;
    }
}
