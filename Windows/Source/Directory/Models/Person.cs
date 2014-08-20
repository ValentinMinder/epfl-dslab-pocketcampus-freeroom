// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using System.Reflection;
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
        /// An URL to the person's picture, if it's visible.
        /// </summary>
        [ThriftField( 11, false, "pictureUrl" )]
        public string PictureUrl { get; set; }

        /// <summary>
        /// The person's roles.
        /// </summary>
        [ThriftField( 12, false, "roles" )]
        public Dictionary<string, PersonRole> Roles { get; set; }

        /// <summary>
        /// The person's homepages.
        /// </summary>
        [ThriftField( 13, false, "homepages" )]
        public Dictionary<string, string> Homepages { get; set; }


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


        /// <summary>
        /// Parses a person from key/value pairs.
        /// </summary>
        public static Person Parse( IDictionary<string, string> pairs )
        {
            var person = new Person();
            var typeInfo = typeof( Person ).GetTypeInfo();

            foreach ( var pair in pairs )
            {
                var prop = typeInfo.DeclaredProperties.FirstOrDefault( p =>
                {
                    var attr = p.GetCustomAttribute<ThriftFieldAttribute>();
                    return attr != null && attr.Name == pair.Key;
                } );
                // thankfully there are only strings in this class, apart from roles & homepages
                if ( prop != null && prop.PropertyType == typeof( string ) )
                {
                    prop.SetValue( person, pair.Value );
                }
            }

            return person;
        }
    }
}