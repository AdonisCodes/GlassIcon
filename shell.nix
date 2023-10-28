{ pkgs ? import <nixpkgs> {} }:

with pkgs;

stdenv.mkDerivation rec {
  name = "scala-dev";

  buildInputs = [
    scala
    sbt
  ];

  shellHook = ''
    export JAVA_HOME=${jre}  # Sets JAVA_HOME to the Java Runtime Environment included with Nixpkgs
  '';
}
