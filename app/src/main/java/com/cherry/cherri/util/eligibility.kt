package com.cherry.cherri.util

import android.util.Log
import com.cherry.cherri.data.Degree
import com.cherry.cherri.ui.profile.Profile

/*
data class Degree(
    val id: Long,
    val name: String,
    val description: String,
    val subjectRequirements: List<SubjectRequirement>,
    val pointRequirement: Int?,
    val pointCalculation: String
)

data class SubjectRequirement(
    val subject: String,
    val orSubject: String? = null,
    val minPoints: Int
)

 */



data class SubjectMark(
    val subject: String,
    val mark: Int
)


fun convertSubjPoints(pcMethod: String, mark: Int, subject: String): Int {
    Log.e("pc: ", pcMethod)
    return when (pcMethod) {
        "PercNSC", "UCTFPS" -> mark
        "WitsAPS" -> when {
            subject == "English" || subject == "Mathematics" || subject == "English HL" || subject == "English FAL" -> when {
                mark >= 90 -> 10
                mark >= 80 -> 9
                mark >= 70 -> 8
                mark >= 60 -> 7
                mark >= 50 -> 4
                mark >= 40 -> 3
                else -> 0
            }
            subject == "Life Orientation" -> when {
                mark >= 90 -> 4
                mark >= 80 -> 3
                mark >= 70 -> 2
                mark >= 60 -> 1
                else -> 0
            }
            else -> when {
                mark >= 90 -> 8
                mark >= 80 -> 7
                mark >= 70 -> 6
                mark >= 60 -> 5
                mark >= 50 -> 4
                mark >= 40 -> 3
                else -> 0
            }
        }
        "APSPlus", "UWCAPS" -> when {
            mark >= 90 -> 8
            mark >= 80 -> 7
            mark >= 70 -> 6
            mark >= 60 -> 5
            mark >= 50 -> 4
            mark >= 40 -> 3
            mark >= 30 -> 2
            mark >= 20 -> 1
            else -> 0
        }
        "APS" -> when {
            mark >= 80 -> 7
            mark >= 70 -> 6
            mark >= 60 -> 5
            mark >= 50 -> 4
            mark >= 40 -> 3
            mark >= 30 -> 2
            mark >= 20 -> 1
            else -> 0
        }
        else -> 0
    }
}

fun calculateTotalPoints(
    pcMethod: String,
    subjectMarks: List<SubjectMark>,
    faculty: String,
    nbtScores: Map<String, Int>
): Int {
    return when (pcMethod) {
        "PercNSC" -> subjectMarks.sumOf { convertSubjPoints(pcMethod, it.mark, it.subject) }
        "APS" -> subjectMarks.sumOf { convertSubjPoints(pcMethod, it.mark, it.subject) }
        "APSPlus" -> subjectMarks.sumOf { convertSubjPoints(pcMethod, it.mark, it.subject) }
        "UWCAPS" -> subjectMarks.sumOf { convertSubjPoints(pcMethod, it.mark, it.subject) }
        "UCTFPS" -> if (faculty == "Health Science") {
            subjectMarks.sumOf { it.mark } + nbtScores.values.sum()
        } else {
            subjectMarks.sumOf { convertSubjPoints(pcMethod, it.mark, it.subject) }
        }
        "WitsAPS" -> subjectMarks.sumOf { convertSubjPoints(pcMethod, it.mark, it.subject) }
        else -> 0
    }
}

fun filterDegreesByEligibility(
    degrees: List<Degree>,
    profile: Profile,
    faculty: String
): List<Degree> {
    // Combine subjects and marks into a single map
    val subjectMarksMap: Map<String, Int> = combineSubjectsAndMarks(profile)

    val subjectMarks = subjectMarksMap.map { (subject, mark) ->
        SubjectMark(subject = subject, mark = mark)
    }

    val nbtScores = profile.nbtScores?.mapValues { (_, score) -> score.toIntOrNull() ?: 0 } ?: emptyMap()

    return degrees.filter { degree ->
        Log.e("deg count", degree.name+" : "+degree.pointRequirement)
        // Check if all subject requirements are met
        /*
        val subjectRequirementsMet = degree.subjectRequirements.all { req ->
            Log.e("req: ", req.subject+" : " +req.orSubject)
            val userSubjectMark = subjectMarks.find {
                Log.e("it subj: ", it.subject)
                it.subject == req.subject || it.subject == req.orSubject
            }

            userSubjectMark != null &&
                    convertSubjPoints(degree.pointCalculation, userSubjectMark.mark, userSubjectMark.subject) >= req.minPoints
        }*/
        val subjectRequirementsMet = if (degree.subjectRequirements.isEmpty() || degree.subjectRequirements[0].subject == null) {
            Log.e("subjectRequirements", "No subject requirements for ${degree.name}")
            true // Automatically pass if there are no subject requirements
        } else {
            Log.e("req size", degree.subjectRequirements.size.toString())

            degree.subjectRequirements.all { req ->
                Log.e("req2: ", req.subject + " : " + req.orSubject)

                val subjectString = req.subject?.toString() ?: "null"

                if (subjectString == "null") {
                    Log.e("Invalid requirement", "Skipping invalid requirement for ${degree.name}")
                    true // Treat invalid requirements as met
                } else {
                    Log.e("not null", subjectString)
                    val userSubjectMark = subjectMarks.find {
                        Log.e("it subj: ", it.subject)
                        it.subject == req.subject || it.subject == req.orSubject
                    }

                    userSubjectMark != null &&
                            convertSubjPoints(
                                degree.pointCalculation,
                                userSubjectMark.mark,
                                userSubjectMark.subject
                            ) >= req.minPoints
                }

            }
        }

        if (!subjectRequirementsMet) {return@filter false}

        // Calculate total points
        val totalPoints = calculateTotalPoints(
            degree.pointCalculation,
            subjectMarks,
            faculty,
            nbtScores
        )

        Log.e("stats: ",totalPoints.toString())
        Log.e("pointReq", degree.pointRequirement.toString())
        // Check if the degree's point requirement is met
        degree.pointRequirement?.let {
            totalPoints >= it
        } ?: true
    }
}


fun combineSubjectsAndMarks(profile: Profile): Map<String, Int> {
    val subjects = profile.subjects ?: return emptyMap()
    val marks = profile.marks ?: return emptyMap()

    return subjects.mapNotNull { (subjectKey, subjectName) ->
        val markKey = subjectKey.replace("subject", "mark")
        val markValue = marks[markKey]?.toIntOrNull()

        if (markValue != null) {
            subjectName to markValue
        } else {
            null // Skip entries where marks are missing or invalid
        }
    }.toMap()
}



