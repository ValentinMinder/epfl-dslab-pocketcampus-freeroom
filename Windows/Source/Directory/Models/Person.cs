// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using ThriftSharp;

namespace PocketCampus.Directory.Models
{
    [ThriftStruct( "Person" )]
    public sealed class Person
    {
        [ThriftField( 1, true, "firstName" )]
        public string FirstName { get; set; }

        [ThriftField( 2, true, "lastName" )]
        public string LastName { get; set; }

        // May not be visible
        [ThriftField( 4, false, "email" )]
        public string EmailAddress { get; set; }

        // May not exist or not be visible
        [ThriftField( 7, false, "officePhoneNumber" )]
        public string OfficePhoneNumber { get; set; }

        // May not exist
        [ThriftField( 8, false, "office" )]
        public string Office { get; set; }

        // May not be visible
        [ThriftField( 11, false, "pictureUrl" )]
        public string PictureUrl { get; set; }

        [ThriftField( 12, false, "roles" )]
        public Dictionary<string, PersonRole> Roles { get; set; }

        [ThriftField( 13, false, "homepages" )]
        public Dictionary<string, string> Homepages { get; set; }


        public string FullName
        {
            get { return FirstName + " " + LastName; }
        }


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