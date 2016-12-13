# Test Plan

## Unite test

* **admin/login**
	|name	|password   |password(encrypted MD5)	|result |
	|-------|------|------|-------|
	|admin 	|NoWater118 |ad8f5a70318ff8a43529a50673b8528e   |Fail   | 
	|adminNoWater 	|NoWater118 |ad8f5a70318ff8a43529a50673b8528e 	| success |
    |adminNoWater   |123456 |e10adc3949ba59abbe56e057f20f883e   |Fail   |
    