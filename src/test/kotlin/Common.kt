fun readResource(name: String) = ClassLoader.getSystemClassLoader().getResource(name).readText()
