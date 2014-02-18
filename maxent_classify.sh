#!/bin/sh

javac maxent_classify.java
javac MaxEnt.java
java maxent_classify $@