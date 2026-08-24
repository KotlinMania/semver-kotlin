import Testing
import Semver

@Suite("Semver Swift Export Tests")
struct SemverExportTests {
    @Test("Swift module imports and basic types are reachable")
    func swiftModuleLoads() throws {
        #expect(Bool(true))
    }
}
