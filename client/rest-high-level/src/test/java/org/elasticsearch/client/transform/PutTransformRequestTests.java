/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.client.transform;

import org.elasticsearch.client.ValidationException;
import org.elasticsearch.client.transform.transforms.TransformConfig;
import org.elasticsearch.client.transform.transforms.TransformConfigTests;
import org.elasticsearch.client.transform.transforms.pivot.PivotConfigTests;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.test.AbstractXContentTestCase;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;

public class PutTransformRequestTests extends AbstractXContentTestCase<PutTransformRequest> {

    public void testValidate() {
        assertFalse(createTestInstance().validate().isPresent());

        TransformConfig config = TransformConfig.builder().setPivotConfig(PivotConfigTests.randomPivotConfig()).build();

        Optional<ValidationException> error = new PutTransformRequest(config).validate();
        assertTrue(error.isPresent());
        assertThat(error.get().getMessage(), containsString("transform id cannot be null"));
        assertThat(error.get().getMessage(), containsString("transform source cannot be null"));
        assertThat(error.get().getMessage(), containsString("transform destination cannot be null"));

        error = new PutTransformRequest(null).validate();
        assertTrue(error.isPresent());
        assertThat(error.get().getMessage(), containsString("put requires a non-null transform config"));
    }

    @Override
    protected PutTransformRequest createTestInstance() {
        return new PutTransformRequest(TransformConfigTests.randomTransformConfig());
    }

    @Override
    protected PutTransformRequest doParseInstance(XContentParser parser) throws IOException {
        return new PutTransformRequest(TransformConfig.fromXContent(parser));
    }

    @Override
    protected boolean supportsUnknownFields() {
        return false;
    }

    @Override
    protected NamedXContentRegistry xContentRegistry() {
        SearchModule searchModule = new SearchModule(Settings.EMPTY, false, Collections.emptyList());
        List<NamedXContentRegistry.Entry> namedXContents = searchModule.getNamedXContents();
        namedXContents.addAll(new TransformNamedXContentProvider().getNamedXContentParsers());

        return new NamedXContentRegistry(namedXContents);
    }
}
