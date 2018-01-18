/*
 *   Copyright 2018 Maximilian Schremser
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.ibm.birt;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "birt")
@Getter
@Setter
@ToString
@Slf4j
@Validated
public class BirtProperties {
    public enum OutputFormat {
        HTML,
        PDF,
        MS_WORD;
    }

    /**
     * the format of the rendered report
     */
    private OutputFormat outputFormat;

    /**
     * the path where the report will be rendered to
     *//* TODO: no Validations found to apply */
    private File outputFile;

    @Valid
    private Report report;

    @Getter
    @Setter
    @ToString
    public static class Report {

        /**
         * the BIRT report file (*.rptdesign)
         * classpath:/path/to/file
         * file:///path/to/file
         * http://server/path/to/file
         */
        private Resource file;

        /**
         * the report parameters in JSON format
         */
        @Valid
        private Param param;

        private Map<String, String> params = new HashMap<>();

        @Getter
        @Setter
        @ToString
        public static class Param {
            /**
             * firstname for the dataset
             */
            private String firstname;
            /**
             * lastname for the dataset
             */
            private String lastname;
            /**
             * company for the dataset
             */
            private String company;
        }
    }

}
