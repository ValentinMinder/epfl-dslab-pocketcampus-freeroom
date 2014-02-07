// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Moodle.Models;

// Plumbing to display courses in LongListSelectors

namespace PocketCampus.Moodle
{
    public sealed class CourseSectionAsGroup : List<CourseFile>
    {
        public string Name { get; set; }

        public CourseSectionAsGroup( CourseSection section )
            : base( section.Files )
        {
            Name = section.Name;
        }
    }
}