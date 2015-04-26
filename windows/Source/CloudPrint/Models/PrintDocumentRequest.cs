// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftStruct( "PrintDocumentRequest" )]
    public sealed class PrintDocumentRequest
    {
        [ThriftField( 1, true, "documentId" )]
        public long DocumentId { get; set; }

        [ThriftField( 5, false, "pageSelection" )]
        public PageRange Range { get; set; }

        [ThriftField( 6, false, "multiPageConfig" )]
        public MultiPageConfig MultiPageConfig { get; set; }

        [ThriftField( 7, false, "doubleSided" )]
        public DoubleSidedConfig? DoubleSidedConfig { get; set; }

        [ThriftField( 8, false, "orientation" )]
        public PageOrientation? Orientation { get; set; }

        [ThriftField( 9, false, "multipleCopies" )]
        public CopiesConfig CopiesConfig { get; set; }

        [ThriftField( 10, false, "colorConfig" )]
        public ColorConfig? ColorConfig { get; set; }
    }
}