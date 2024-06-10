#!/bin/bash

taskkill //F //IM "SoapUI-5.7.0.exe"
cp "$PWD/target/main_groovy_scripts_library-2.0.jar" "C:\Program Files\SmartBear\SoapUI-5.7.0\bin\ext"
