#!/bin/bash

taskkill //F //IM "SoapUI-5.7.0.exe"
cp "$PWD/target/rir2_library-1.0.jar" "C:\Program Files\SmartBear\SoapUI-5.7.0\bin\ext"