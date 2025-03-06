package com.cherry.cherri.data

import android.util.Log
import com.cherry.cherri.BuildConfig
import org.neo4j.driver.*

val neo4jUrl = BuildConfig.NEO4J_URI
val neo4jUsername = BuildConfig.NEO4J_USERNAME
val neo4jPassword = BuildConfig.NEO4J_PASSWORD


fun getUniversitiesWithFaculties(): List<UniversityWithFaculties> {
    val driver: Driver = GraphDatabase.driver(neo4jUrl, AuthTokens.basic(neo4jUsername, neo4jPassword))
    val session: Session = driver.session()

    try {
        // Run the Cypher query to get universities and their faculties
        val result = session.run(
            """
            MATCH (u:University)-[:HAS_FACULTY]->(f:Faculty)
            RETURN u, collect(f) AS faculties
            ORDER BY u.ranking
            """
        )

        // Map the result records to the desired structure
        return result.list().map { record ->
            val universityNode = record.get("u").asNode()
            val facultiesNodes = record.get("faculties").asList() {it.asNode()}

            // Map faculties to a list of Faculty objects
            val faculties = facultiesNodes.map { facultyNode ->
                Faculty(
                    id = facultyNode.id(),
                    name = facultyNode.get("name").asString()
                )
            }

            // Return a UniversityWithFaculties object
            UniversityWithFaculties(
                id = universityNode.id(),
                name = universityNode.get("name").asString(),
                location = universityNode.get("location").asString(),
                logoUrl = universityNode.get("logoUrl").asString(),
                appUrl = universityNode.get("appUrl").asString(),
                faculties = faculties
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return emptyList()
    } finally {
        session.close()
    }

}

fun getDegreesForFaculty(facultyId: Long): List<Degree> {
    val driver: Driver = GraphDatabase.driver(neo4jUrl, AuthTokens.basic(neo4jUsername, neo4jPassword))
    val session: Session = driver.session()


    try {
        // Cypher query to get all Degree nodes connected to a Faculty node via HAS_DEGREE relationship
        val cypherQuery = """
                      MATCH (f:Faculty)-[:HAS_DEGREE]->(d:Degree)
      MATCH (f:Faculty)-[:USES_PC]->(pc:PointCalculation)
      WHERE id(f) = $facultyId
      OPTIONAL MATCH (d)-[sr:SUBJECT_REQUIREMENT]->(s:Subject)
      OPTIONAL MATCH (d)-[pr:POINT_REQUIREMENT]->(pc:PointCalculation)
      RETURN d,
             collect({
               minPoints: sr.minPoints,
               orSubject: sr.orSubject,
               subject: s.name
             }) AS subjectRequirements,
             pr.minPoints AS pointRequirement,
             pc.name as pointCalculation
      ORDER BY d.name
            """

        // Execute the query and process the results
        val result: List<Record> = session.run(cypherQuery).list()
        val degrees = mutableListOf<Degree>()

        // Loop through the results and map them to Degree objects
        result.forEach { record ->
            val degreeNode = record.get("d").asNode()

            val subjectRequirementsRaw = record.get("subjectRequirements").asList { value ->
                // Each 'value' is expected to be a map with the keys: minPoints, orSubject, subject
                val subject = value.get("subject")?.asString() // Nullable
                val orSubject = value.get("orSubject")?.asString() // Nullable
                val minPoints = value.get("minPoints")?.takeIf { !it.isNull }?.asInt() ?: 0

                if (subject == null || subject.isEmpty()) {
                    Log.e("null detected", "det")
                    null // Skip invalid entries
                } else {
                    SubjectRequirement(
                        minPoints = minPoints,
                        orSubject = orSubject,
                        subject = subject
                    )
                }
            }.filterNotNull()

// Convert to Array if necessary
            val subjectRequirements: Array<SubjectRequirement> = subjectRequirementsRaw.toTypedArray()
            /*println("Degree Node: ${record.get("d")}")
            println("Subject Requirements: ${record.get("subjectRequirements")}")
            println("Point Requirement: ${record.get("pointRequirement")}")
            println("Point Calculation: ${record.get("pointCalculation")}")*/
            val degree = Degree(
                id = degreeNode.id(),
                name = degreeNode.get("name").asString(),
                description = degreeNode.get("description").asString(),
                subjectRequirements = subjectRequirements,
                pointRequirement = record.get("pointRequirement")?.takeIf { !it.isNull }?.asInt() ?: 0,
                pointCalculation = record.get("pointCalculation").asString()
            )
            degrees.add(degree)
        }

        return degrees

    } catch (e: Exception) {
        e.printStackTrace()
        return emptyList() // Return an empty list in case of error
    } finally {
        session.close()
    }
}

data class University(
    val id: Int,
    val name: String,
    val logo: Int,
    val appUrl: String)

data class UniversityWithFaculties(
    val id: Long,
    val name: String,
    val location: String,
    val logoUrl: String,
    val appUrl: String,
    val faculties: List<Faculty>
)

data class Faculty(
    val id: Long,
    val name: String
)

data class SubjectRequirement(
    val minPoints: Int,
    val orSubject: String?,
    val subject: String

)

data class Degree(
    val id: Long,
    val name: String,
    val description: String,
    val subjectRequirements: Array<SubjectRequirement>,
    val pointRequirement: Int?,
    val pointCalculation: String
)
