# Eggs

Eggs is an Intellij IDEA plugin that allows you to write Java code to interact with IDEA.

# Todo

```
v1.0.5
default execute unit
- generate getter & setter
- generate SerialVersionUID
- pojo class -> json
- json -> pojo class
```





# Usage

## Write execute unit

**Write your code in the EggsSettings panel，the MainClass must contain method  `void main(Map<String,Object>)context`**

![image-20210203142929543](-./doc/images/Eggs-usage.gif)

```java
package com.github.hexffff0.incubator.codeutils.copymethod;

import java.util.List;
import java.util.Map;
import com.github.hexffff0.eggs.utils.JavaUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
public class CopyMethod {

    public void main(Map<String, Object> context) {
        AnActionEvent event = (AnActionEvent) context.get("AnActionEvent");
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        PsiClass selectedClass = JavaUtils.selectClass("select class", project);
        List<PsiMethod> methods = JavaUtils.selectMethods(selectedClass, "select method", true, true);
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        PsiFile file = event.getData(CommonDataKeys.PSI_FILE);
        methods.forEach(method -> JavaUtils.writeToCaret(method.getText(), file, editor));
    }
}
```

## How to write execute unit efficiently

Clone [Incubator](https://github.com/hexffff0/incubator) project，write your execute unit in the project (you can use all the classes you found in this project)

![image-20210208134928962](./doc/images/example-8.png)

# Installation

## Jetbrains plugin market

- 

## Releases

Download from the [Releases](https://github.com/hexffff0/eggs/releases) page

## Compile from source code

```shell
git clone https://github.com/hexffff0/eggs.git
cd eggs
./gradlew buildPlugin
# the artifact will be placed on the ./build/distributions directory
```

# License

``` 
Copyright 2021 com.github.hexffff0

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```

