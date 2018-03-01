package nu.peg.web.files.exception

import java.io.IOException

class SubPathIsNotInBasePathException : IOException("The sub path is not contained in the base path. Directory traversal?")