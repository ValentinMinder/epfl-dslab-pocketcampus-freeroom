// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.IO;
using System.Threading.Tasks;

namespace PocketCampus.CloudPrint.Services
{
    public interface IFileLoader
    {
        Task<Stream> GetFileAsync( Uri uri );
        Task DeleteFileAsync( Uri uri );
    }
}