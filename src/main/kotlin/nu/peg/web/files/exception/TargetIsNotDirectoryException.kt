package nu.peg.web.files.exception

import java.io.IOException

class TargetIsNotDirectoryException : IOException("The target path is not a directory")