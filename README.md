PinchToClose
============

Android library to close an activity by simply pinching it with 3+ fingers! Why is it convenient and better than a back button? Firstly, we use our ever-increasing-size phones with two hands anyway; secondly, it is cooler!

![Screenshot][1]![Screenshot][2]![Screenshot][3]

Usage
--------

1) Add PinchToClose as a library to your project
or less recommended
copy lt.marius.pinchtoclose package to your Android project

2) Set your activity theme to Transparent PinchToClose theme ```android:theme="@style/Transparent"``` or use the example provided in the ```styles.xml```

3) Add the following code to the activity that you want to pinch-to-close:
```java
PinchToClose.init(this);
````
If you want to have a custom finish code e.g. to set a result before closing the activity:
```java
PinchToClose.init(this, false, new CustomFinishCallback() {
			
			@Override
			public void finish(Activity activity) {
				Toast t = Toast.makeText(getApplicationContext(), "Activity closed", Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 20);
				t.show();
			}
		});
```

Note: ```this``` refers to _Activity_ object; ```init()``` must be called after ```setContentView()```

For more examples on usage see ```MainActivity.java```

This library is dependent on the [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids/#nine-old-androids) library for targeting pre-Honeycomb devices.

Demo
----
In "Google Play" https://play.google.com/store/apps/details?id=lt.marius.pinchtoclose

Author
-------
Marius Noreikis - marius.noreikis@gmail.com

License
--------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
   
Short summary of the License at

   https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


[1]: http://imageshack.com/a/img35/8466/ax0d.png
[2]: http://imageshack.com/a/img822/5497/j3ds.png
[3]: http://imageshack.com/a/img853/2023/1lo4.png
