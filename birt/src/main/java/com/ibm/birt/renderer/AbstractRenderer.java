/*
 *
 *  *   Copyright 2018 Maximilian Schremser
 *  *
 *  *   Licensed under the Apache License, Version 2.0 (the "License");
 *  *   you may not use this file except in compliance with the License.
 *  *   You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *   Unless required by applicable law or agreed to in writing, software
 *  *   distributed under the License is distributed on an "AS IS" BASIS,
 *  *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *   See the License for the specific language governing permissions and
 *  *   limitations under the License.
 *
 */

package com.ibm.birt.renderer;

import com.ibm.birt.bean.BirtConfiguration;
import com.ibm.birt.bean.BirtProperties;
import lombok.Getter;

@Getter
public abstract class AbstractRenderer implements IRenderer {
    BirtConfiguration configuration;

    public AbstractRenderer(BirtConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getFileEnding(BirtProperties.OutputFormat outputFormat) {
        switch (outputFormat) {
            case HTML:
                return "html";
            case PDF:
                return "pdf";
            case MS_WORD:
                return "docx";
            default:
                return "txt";
        }
    }
}
