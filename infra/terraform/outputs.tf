output "eks_cluster_name" {
  value = aws_eks_cluster.main.name
}

output "backend_ecr_repository_url" {
  value = aws_ecr_repository.backend.repository_url
}

output "frontend_ecr_repository_url" {
  value = aws_ecr_repository.frontend.repository_url
}

output "tracks_bucket_name" {
  value = aws_s3_bucket.tracks.bucket
}

output "images_bucket_name" {
  value = aws_s3_bucket.images.bucket
}

output "database_endpoint" {
  value = aws_db_instance.postgres.address
}

output "redis_endpoint" {
  value = aws_elasticache_cluster.redis.cache_nodes[0].address
}

output "track_events_queue_url" {
  value = aws_sqs_queue.track_events.url
}

output "track_events_topic_arn" {
  value = aws_sns_topic.track_events.arn
}

output "backend_irsa_role_arn" {
  value = aws_iam_role.backend_irsa.arn
}
