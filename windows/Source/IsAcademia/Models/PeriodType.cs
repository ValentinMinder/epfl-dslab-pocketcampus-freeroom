// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.IsAcademia.Models
{
    [ThriftEnum]
    public enum PeriodType
    {
        Lecture = 0,
        Exercises = 1,
        Lab = 2,
        Project = 3,
        OralExam = 4,
        WrittenExam = 5
    }
}