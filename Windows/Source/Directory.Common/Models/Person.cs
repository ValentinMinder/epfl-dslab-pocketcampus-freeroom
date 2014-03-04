// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    /// <summary>
    /// A person in the EPFL directory.
    /// </summary>
    [ThriftStruct( "Person" )]
    public sealed class Person
    {
        /// <summary>
        /// The person's first name.
        /// </summary>
        [ThriftField( 1, true, "firstName" )]
        public string FirstName { get; set; }

        /// <summary>
        /// The person's last name.
        /// </summary>
        [ThriftField( 2, true, "lastName" )]
        public string LastName { get; set; }

        /// <summary>
        /// The person's e-mail address, if it's visible.
        /// </summary>
        [ThriftField( 4, false, "email" )]
        public string EmailAddress { get; set; }

        /// <summary>
        /// The person's website, if any.
        /// </summary>
        [ThriftField( 5, false, "web" )]
        public string Website { get; set; }

        /// <summary>
        /// The person's private phone number, if it's visible.
        /// </summary>
        [ThriftField( 6, false, "privatePhoneNumber" )]
        public string PrivatePhoneNumber { get; set; }

        /// <summary>
        /// The person's office phone number, if it's visible.
        /// </summary>
        [ThriftField( 7, false, "officePhoneNumber" )]
        public string OfficePhoneNumber { get; set; }

        /// <summary>
        /// The person's office, if any.
        /// </summary>
        [ThriftField( 8, false, "office" )]
        public string Office { get; set; }

        /// <summary>
        /// The organizational units to which the person belongs (e.g. IN-BA3, DSLAB).
        /// </summary>
        [ThriftField( 10, false, "OrganisationalUnit" )]
        public string[] Units { get; set; }

        /// <summary>
        /// An URL to the person's picture, if it's visible.
        /// </summary>
        [ThriftField( 11, false, "pictureUrl" )]
        public string PictureUrl { get; set; }


        /// <summary>
        /// The person's full name.
        /// </summary>
        /// <remarks>
        /// Not in the Thrift interface.
        /// </remarks>
        public string FullName
        {
            get { return FirstName + " " + LastName; }
        }
    }
}