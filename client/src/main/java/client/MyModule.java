/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import client.scenes.AddTagCtrl;
import client.scenes.LanguageIndicatorCtrl;
import client.utils.*;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

import client.scenes.MainCtrl;
import com.google.inject.name.Names;
import javafx.stage.Stage;

import java.io.File;
import java.util.Locale;
import java.util.Properties;

public class MyModule implements Module {
    /**
     * Configure the dependency graph
     * @param binder Binder that is used to form the dependency graph for the injector
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(Locale.class).annotatedWith(Names.named("defaultLocale")).toInstance(Locale.of("en", "GB"));
        Properties properties = new ConfigUtils().easyLoadProperties();
        binder.bind(Properties.class).annotatedWith(Names.named("config")).toInstance(properties);
        Names.bindProperties(binder, properties);

        binder.bind(Translation.class).in(Scopes.SINGLETON);
        binder.bind(ObservableResourceFactory.class).in(Scopes.SINGLETON);
        binder.bind(ManagementOverviewUtils.class).in(Scopes.SINGLETON);
        binder.bind(LanguageIndicatorCtrl.class).in(Scopes.SINGLETON);
        binder.bind(File.class).annotatedWith(Names.named("dir")).toInstance(new File(Translation.LANGUAGE_PATH));
        binder.bind(WebSocketUtils.class).in(Scopes.SINGLETON);
        binder.bind(LPUtils.class).in(Scopes.SINGLETON);
        binder.bind(AppStateManager.class).in(Scopes.SINGLETON);
        binder.bind(TransferMoneyUtils.class).in(Scopes.SINGLETON);
        binder.bind(AddTagCtrl.class).in(Scopes.SINGLETON);
        binder.bind(Stage.class).in(Scopes.SINGLETON);

        binder.bind(MainCtrl.class).in(Scopes.SINGLETON);
    }
}