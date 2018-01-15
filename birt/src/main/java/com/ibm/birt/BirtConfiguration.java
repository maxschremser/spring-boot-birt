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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@EnableConfigurationProperties(BirtProperties.class)
@PropertySources( {
        @PropertySource("classpath:birt.properties")
})
@Getter
@Slf4j
public class BirtConfiguration {

    private BirtProperties properties;

    public BirtConfiguration(BirtProperties properties) {
        this.properties = properties;
        log.info(properties.toString());
    }
}
