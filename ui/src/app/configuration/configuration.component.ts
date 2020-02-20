/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Component, ViewChild} from '@angular/core';

import { ConfigurationService } from './shared/configuration.service';
import { StreampipesPeContainer } from "./shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "./shared/streampipes-pe-container-configs";
import { MatPaginator } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
    templateUrl: './configuration.component.html',
    styleUrls: ['./configuration.component.css'],
    animations: [
        trigger('detailExpand', [
            state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
            state('expanded', style({height: '*'})),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
        ]),
    ]
})
export class ConfigurationComponent {

    selectedIndex: number = 0;

    constructor() {
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

}