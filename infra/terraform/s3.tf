resource "aws_s3_bucket" "tracks" {
  bucket = "${local.name_prefix}-tracks"
}

resource "aws_s3_bucket" "images" {
  bucket = "${local.name_prefix}-images"
}

resource "aws_s3_bucket_public_access_block" "tracks" {
  bucket                  = aws_s3_bucket.tracks.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_public_access_block" "images" {
  bucket                  = aws_s3_bucket.images.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_versioning" "tracks" {
  bucket = aws_s3_bucket.tracks.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "tracks" {
  bucket = aws_s3_bucket.tracks.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "images" {
  bucket = aws_s3_bucket.images.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}
