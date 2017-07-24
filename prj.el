;; jartest
;; Test how to use the emacs JDEE and Makefile to compile and run java program
(jde-project-file-version "1.0")
(jde-set-variables)

;;;;;;;;;;;;;;;; Compile arguments ;;;;;;;;;;;;;;;;

; sourcepath
; Makefile: "javac -sourcepath ./src:./anothersrc /media/uraplutonium/Cardboard/Workspace/java-compile/jartest/src/pack/Test.java"
(setq jde-compile-option-sourcepath (quote ("/media/uraplutonium/Workstation/Workspace/teabag-ml/src/main/java")))

; classpath
; Makefile: "javac -classpath ./lib/swt.jar /media/uraplutonium/Cardboard/Workspace/java-compile/jartest/src/pack/Test.java"
(setq jde-compile-option-classpath (quote ("/media/uraplutonium/Workstation/Applications/maven-repo/org/python/jython/2.5.3/jython-2.5.3.jar")))

; directory
; Makefile: "javac -d ./bin /media/uraplutonium/Cardboard/Workspace/java-compile/jartest/src/pack/Test.java"
(setq jde-compile-option-directory "/media/uraplutonium/Workstation/Workspace/teabag-ml/target/classes")

;;;;;;;;;;;;;;;; Runtime arguments ;;;;;;;;;;;;;;;;

; working directory
; Makefile: "cd /media/uraplutonium/Cardboard/Workspace/java-compile/jartest pack.DMLA"
(setq jde-run-working-directory "/media/uraplutonium/Workstation/Workspace/teabag-ml")

; classpath
; Makefile: "java -classpath /media/uraplutonium/Workstation/Workspace/teabag-ml/target/classes:/media/uraplutonium/Workstation/Applications/maven-repo/org/python/jython/2.5.3/jython-2.5.3.jar pack.DMLA"
(setq jde-run-option-classpath (quote ("/media/uraplutonium/Workstation/Workspace/teabag-ml/target/classes" "/media/uraplutonium/Workstation/Applications/maven-repo/org/python/jython/2.5.3/jython-2.5.3.jar")))

; arguments
; Makefile: "java pack.DMLA arg1 arg2"
(setq jde-run-option-application-args (quote ("arg1" "arg2")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(jde-set-variables '(jde-enable-abbrev-mode t))
