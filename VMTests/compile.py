from VMCodeCompiler.compiler import Compiler

import sys
c=Compiler(sys.argv[1])
c.compile()
print(c.get_code())
c.write_to_file(sys.argv[2])